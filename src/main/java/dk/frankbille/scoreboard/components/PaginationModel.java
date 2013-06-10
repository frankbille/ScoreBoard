/*
 * ScoreBoard
 * Copyright (C) 2012-2013 Frank Bille
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dk.frankbille.scoreboard.components;

import java.util.List;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

public class PaginationModel<T> extends LoadableDetachableModel<List<T>> {
	private static final long serialVersionUID = 1L;

	private IModel<List<T>> nestedModel;

	private int page = 1;

	private int resultsPerPage;

	public PaginationModel(IModel<List<T>> nestedModel, int startPage, int resultsPerPage) {
		this.nestedModel = nestedModel;
		page = startPage;
		this.resultsPerPage = resultsPerPage;
		
		if (page < 1) {
			page = 1;
		}
	}
	
	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public boolean isPaginationNeeded() {
		return getMaxPage() > 1;
	}
	
	public boolean isPreviousPossible() {
		return page > 1;
	}
	
	public int previousPage() {
		if (isPreviousPossible() == false) {
			throw new IllegalStateException("Can not go to previous when already on first page");
		}
		
		page--;
		
		return getPage();
	}
	
	public boolean isNextPossible() {
		return page < getMaxPage();
	}
	
	public int getMaxPage() {
		List<T> fullList = nestedModel.getObject();
		int maxRows = Math.max(fullList.size(), resultsPerPage);
		return (int) Math.ceil((double)maxRows/(double)resultsPerPage);
	}
	
	public int nextPage() {
		if (isNextPossible() == false) {
			throw new IllegalStateException("Can not go to next page when already on last page");
		}
		
		page++;
		
		return getPage();
	}
	
	public int getResultsPerPage() {
		return resultsPerPage;
	}
	
	@Override
	protected List<T> load() {
		List<T> fullList = nestedModel.getObject();
		int fromIndex = 0;
		int toIndex = resultsPerPage;
		if (page > 1) {
			fromIndex = (page-1)*resultsPerPage;
			toIndex = fromIndex+resultsPerPage;
		}
		if (toIndex >= fullList.size()) {
			toIndex = fullList.size();
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		return fullList.subList(fromIndex, toIndex);
	}
	
	@Override
	public void onDetach() {
		nestedModel.detach();
	}

}
