package com.kunlunsoft.design.responsibility.filter;


import java.util.ArrayList;
import java.util.List;

public class CalculateFilterChain {
    private List<ICalculateDealFilter> filterList = new ArrayList<>();
    private int index = 0;
    private boolean hasAddDefaultFilter = false;

    public CalculateFilterChain addFilter(ICalculateDealFilter filter) {
        if (hasAddDefaultFilter) {
            throw new RuntimeException("自定义过滤器必须在默认过滤器之前添加");
        }
        this.filterList.add(filter);
        return this;
    }


    public CalculateFilterChain addDefaultFilter(ICalculateDealFilter filter) {
        this.filterList.add(filter);
        hasAddDefaultFilter = true;
        return this;
    }

    public void reset() {
        this.index = 0;
    }

    private ICalculateDealFilter next() {
        if (index == filterList.size()) {
            return null;
        }
        ICalculateDealFilter filter = filterList.get(index);
        index++;
        return filter;

    }

    public void doFilter(CalculateInfo calculateInfo, CalculateRequestDto calculateRequestDto, CalculateResponse calculateResponse) {
//        this.init();
        ICalculateDealFilter filter = next();
        if (null == filter) {
            System.out.println("结束 index :" + index);
            return;
        }
        filter.doFilter(calculateInfo, calculateRequestDto, calculateResponse, this);
    }


}
