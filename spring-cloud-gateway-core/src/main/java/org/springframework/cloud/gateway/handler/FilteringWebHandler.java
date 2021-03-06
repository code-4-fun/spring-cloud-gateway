/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.cloud.gateway.handler;

import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.api.RouteLocator;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.WebFilterFactory;
import org.springframework.cloud.gateway.model.Route;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.handler.WebHandlerDecorator;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import reactor.core.publisher.Mono;

/**
 * WebHandler that delegates to a chain of {@link GlobalFilter} instances and
 * {@link WebFilterFactory} instances then to the target {@link WebHandler}.
 *
 * @author Rossen Stoyanchev
 * @author Spencer Gibb
 * @since 0.1
 */
public class FilteringWebHandler extends WebHandlerDecorator {
	protected final Log logger = LogFactory.getLog(getClass());

	private final GatewayProperties gatewayProperties;
	private final RouteLocator routeLocator;

	public FilteringWebHandler(GatewayProperties gatewayProperties, RouteLocator routeLocator) {
		this(new EmptyWebHandler(), gatewayProperties, routeLocator);
	}

	public FilteringWebHandler(WebHandler targetHandler, GatewayProperties gatewayProperties,
							   RouteLocator routeLocator) {
		super(targetHandler);
		this.gatewayProperties = gatewayProperties;
		this.routeLocator = routeLocator;
	}

    /* TODO: relocate @EventListener(RefreshRoutesEvent.class)
    void handleRefresh() {
        this.combinedFiltersForRoute.clear();
    }*/

	@Override
	public Mono<Void> handle(ServerWebExchange exchange) {
		Optional<Route> route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
		List<WebFilter> webFilters = route.get().getWebFilters();

		logger.debug("Sorted webFilterFactories: "+ webFilters);

		return new DefaultWebFilterChain(webFilters, getDelegate()).filter(exchange);
	}

	private static class DefaultWebFilterChain implements WebFilterChain {

		private int index;
		private final List<WebFilter> filters;
		private final WebHandler delegate;

		public DefaultWebFilterChain(List<WebFilter> filters, WebHandler delegate) {
			this.filters = filters;
			this.delegate = delegate;
		}

		@Override
		public Mono<Void> filter(ServerWebExchange exchange) {
			if (this.index < filters.size()) {
				WebFilter filter = filters.get(this.index++);
				return filter.filter(exchange, this);
			}
			else {
				return this.delegate.handle(exchange);
			}
		}
	}

	private static class EmptyWebHandler implements WebHandler {
		@Override
		public Mono<Void> handle(ServerWebExchange exchange) {
			return Mono.empty();
		}
	}

}
