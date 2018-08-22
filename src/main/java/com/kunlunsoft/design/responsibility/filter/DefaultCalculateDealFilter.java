package com.kunlunsoft.design.responsibility.filter;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DefaultCalculateDealFilter implements ICalculateDealFilter {
    @Resource

    @Override
    public void doFilter(CalculateInfo calculateInfo, CalculateRequestDto calculateRequestDto, CalculateResponse calculateResponse, CalculateFilterChain filterChain) {
        filterChain.doFilter(calculateInfo, calculateRequestDto, calculateResponse);//实际上此处没有任何逻辑,因为这是最后一个 filter

        //真正干活
    }
}
