package com.kunlunsoft.design.responsibility.filter;


public interface ICalculateDealFilter {
    void doFilter(CalculateInfo calculateInfo, CalculateRequestDto calculateRequestDto, CalculateResponse calculateResponse, CalculateFilterChain filterChain);
}
