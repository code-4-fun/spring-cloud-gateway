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

package org.springframework.cloud.gateway.handler.predicate;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.gateway.handler.predicate.BeforeRequestPredicateFactory.DATETIME_KEY;
import static org.springframework.cloud.gateway.handler.predicate.BetweenRequestPredicateFactoryTests.getRequest;
import static org.springframework.cloud.gateway.handler.predicate.BetweenRequestPredicateFactoryTests.minusHours;
import static org.springframework.cloud.gateway.handler.predicate.BetweenRequestPredicateFactoryTests.minusHoursMillis;
import static org.springframework.cloud.gateway.handler.predicate.BetweenRequestPredicateFactoryTests.plusHours;
import static org.springframework.cloud.gateway.handler.predicate.BetweenRequestPredicateFactoryTests.plusHoursMillis;
import static org.springframework.tuple.TupleBuilder.tuple;

/**
 * @author Spencer Gibb
 */
public class BeforeRequestPredicateFactoryTests {

	@Test
	public void beforeStringWorks() {
		String dateString = minusHours(1);

		boolean result = runPredicate(dateString);

		assertThat(result).isFalse();
	}

	@Test
	public void afterStringWorks() {
		String dateString = plusHours(1);

		boolean result = runPredicate(dateString);

		assertThat(result).isTrue();
	}

	@Test
	public void beforeEpochWorks() {
		String dateString = minusHoursMillis(1);

		final boolean result = runPredicate(dateString);

		assertThat(result).isFalse();
	}

	@Test
	public void afterEpochWorks() {
		String dateString = plusHoursMillis(1);

		final boolean result = runPredicate(dateString);

		assertThat(result).isTrue();
	}

	private boolean runPredicate(String dateString) {
		return new BeforeRequestPredicateFactory().apply(tuple().of(DATETIME_KEY, dateString)).test(getRequest());
	}
}
