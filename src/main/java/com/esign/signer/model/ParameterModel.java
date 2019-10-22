package com.esign.signer.model;

import com.esign.signer.base.SearchType;

public class ParameterModel {

    private String searchValue;
    public ParameterModel() { 
	}
    public ParameterModel(String searchValue, SearchType searchType) {
		super();
		this.searchValue = searchValue;
		this.searchType = searchType;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public SearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	private SearchType searchType;

 
	 
}
