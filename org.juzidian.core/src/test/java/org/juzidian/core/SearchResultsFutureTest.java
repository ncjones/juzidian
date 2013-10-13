/*
 * Copyright Nathan Jones 2013
 *
 * This file is part of Juzidian.
 *
 * Juzidian is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Juzidian is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Juzidian.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.juzidian.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SearchResultsFutureTest {

	@Mock
	private SearchCanceller mockCanceller;

	@Mock
	private Future<SearchResults> mockResultsFuture;

	private SearchResultsFuture searchResultsFuture;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		searchResultsFuture = new SearchResultsFuture(mockResultsFuture, mockCanceller);
	}

	@Test
	public void getResultsShouldGetSearchResultsFromFuture() throws Exception {
		SearchResults mockSearchResults = Mockito.mock(SearchResults.class);
		when(mockResultsFuture.get()).thenReturn(mockSearchResults);
		SearchResults searchResults = searchResultsFuture.getResults();
		assertThat(searchResults, is(mockSearchResults));
	}

	@Test(expected = RuntimeException.class)
	public void getResultsShouldThrowRuntimeExceptionWhenFutureThrowsInterruptedException() throws Exception {
		when(mockResultsFuture.get()).thenThrow(new InterruptedException());
		searchResultsFuture.getResults();
	}

	@Test
	public void getResultsShouldSetInterruptedStatusWhenFutureThrowsInterruptedException() throws Exception {
		when(mockResultsFuture.get()).thenThrow(new InterruptedException());
		try {
			searchResultsFuture.getResults();
		} catch (RuntimeException e) {
		}
		assertThat(Thread.currentThread().isInterrupted(), is(true));
	}

	@Test(expected = SearchCancelledException.class)
	public void getResultsShouldThrowSearchCancelledExceptionWhenTaskCancelled() throws Exception {
		when(mockResultsFuture.get()).thenThrow(new ExecutionException(new SearchCancelledException()));
		searchResultsFuture.getResults();
	}

	@Test(expected = RuntimeException.class)
	public void getResultsShouldThrowSearchCancelledExceptionWhenTaskFails() throws Exception {
		when(mockResultsFuture.get()).thenThrow(new ExecutionException(new NullPointerException()));
		searchResultsFuture.getResults();
	}

	@Test
	public void cancelShouldCancelCanceller() throws Exception {
		mockCanceller.cancel();
		verify(mockCanceller).cancel();
	}

}
