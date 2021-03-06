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

package org.springframework.cloud.gateway.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.server.WebFilter;

/**
 * @author Spencer Gibb
 */
public class Route {

	private final String id;

	private final URI uri;

	private final RequestPredicate requestPredicate;

	private final List<WebFilter> webFilters;

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder(RouteDefinition routeDefinition) {
		return new Builder()
				.id(routeDefinition.getId())
				.uri(routeDefinition.getUri());
	}

	public Route(String id, URI uri, RequestPredicate requestPredicate, List<WebFilter> webFilters) {
		this.id = id;
		this.uri = uri;
		this.requestPredicate = requestPredicate;
		this.webFilters = webFilters;
	}

	public static class Builder {
		private String id;

		private URI uri;

		private RequestPredicate requestPredicate;

		private List<WebFilter> webFilters = new ArrayList<>();

		private Builder() {}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder uri(URI uri) {
			this.uri = uri;
			return this;
		}

		public Builder requestPredicate(RequestPredicate requestPredicate) {
			this.requestPredicate = requestPredicate;
			return this;
		}

		public Builder webFilters(List<WebFilter> webFilters) {
			this.webFilters = webFilters;
			return this;
		}

		public Builder add(WebFilter webFilter) {
			this.webFilters.add(webFilter);
			return this;
		}

		public Route build() {
			Assert.notNull(this.id, "id can not be null");
			Assert.notNull(this.uri, "uri can not be null");
			//TODO: Assert.notNull(this.requestPredicate, "requestPredicates can not be null");

			return new Route(this.id, this.uri, this.requestPredicate, this.webFilters);
		}
	}

	public String getId() {
		return this.id;
	}

	public URI getUri() {
		return this.uri;
	}

	public RequestPredicate getRequestPredicate() {
		return this.requestPredicate;
	}

	public List<WebFilter> getWebFilters() {
		return Collections.unmodifiableList(this.webFilters);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Route route = (Route) o;
		return Objects.equals(id, route.id) &&
				Objects.equals(uri, route.uri) &&
				Objects.equals(requestPredicate, route.requestPredicate) &&
				Objects.equals(webFilters, route.webFilters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, uri, requestPredicate, webFilters);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Route{");
		sb.append("id='").append(id).append('\'');
		sb.append(", uri=").append(uri);
		sb.append(", requestPredicates=").append(requestPredicate);
		sb.append(", webFilters=").append(webFilters);
		sb.append('}');
		return sb.toString();
	}
}
