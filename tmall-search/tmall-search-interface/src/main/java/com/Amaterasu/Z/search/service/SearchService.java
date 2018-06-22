package com.Amaterasu.Z.search.service;

import com.Amaterasu.Z.pojo.ResponseResult;
import com.Amaterasu.Z.pojo.SearchResult;

public interface SearchService {

	public ResponseResult importItems();

	public SearchResult search(String keyword, Integer page, Integer rows);
}
