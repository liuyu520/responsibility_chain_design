package com.kunlunsoft.design.responsibility.filter.impl;

import com.common.util.RedisCacheUtil;
import com.kunlunsoft.design.responsibility.filter.CalculateFilterChain;
import com.kunlunsoft.design.responsibility.filter.ICalculateDealFilter;
import oa.util.SpringMVCUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/***
 * 新购和续购,3年打九折
 */
@Component
public class DiscountFilter implements ICalculateDealFilter {
    private static final Logger log = Logger.getLogger(DiscountFilter.class);
    @Resource
    private ProductBusiness productBusiness;
    @Resource
    private RequestSafeThreadParamDto requestSafeThreadParamDto;

    @Override
    public void doFilter(CalculateInfo calculateInfo, CalculateRequestDto calculateRequestDto, CalculateResponse calculateResponse, CalculateFilterChain filterChain) {

        filterChain.doFilter(calculateInfo, calculateRequestDto, calculateResponse);

        if (Constant.BUY_TYPE_BUY_MORE.compareTo(calculateInfo.getBuyType()) == 0) {
            return;
        }

        ProductDetailsDto productDetail = productBusiness.queryNewProductDetailById(calculateInfo.getProductId().toString(), true, SpringMVCUtil.getKeyCache(requestSafeThreadParamDto.getCid(), Constant.REDIS_KEY_ACCESS_TOKEN));
        if (!PurchaseBusiness.isXXXProduct(productDetail.getValue().getProductSpec().getIsvAppId())) {
            return;
        }
        String startDate = calculateInfo.getStartDate();
        String endDate = calculateInfo.getEndDate();
        Date startDatetime = DateTimeUtil.getDate4Str(startDate);
        int deltaYears = DateTimeUtil.getYear(endDateTime) - DateTimeUtil.getYear(startDatetime);
        int buyYears = 3;
        if (deltaYears < buyYears) {
            return;
        }
        Date dateAfter3years = DateTimeUtil.getDateAfterByYear(startDatetime, buyYears);
        if (dateAfter3years.getTime() > endDateTime.getTime()) {
            return;
        }
        //需要九折
        CalculatePriceDto calculatePriceDto = calculateResponse.getCalculatePriceDto();
        PriceResult priceResult = calculatePriceDto.value;
        BigDecimal discountRate = new BigDecimal(RedisCacheUtil.getDiscountWhenBuyThreeYears());
        BigDecimal newPayPrice = priceResult.getPrice().multiply(discountRate);
        BigDecimal newDiscountPrice = priceResult.getPrice().subtract(newPayPrice);

        BigDecimal oldDiscount = priceResult.discount;
        if (null == oldDiscount) {
            priceResult.setDiscount(newDiscountPrice);
        } else {
            priceResult.setDiscount(oldDiscount.add(newDiscountPrice));
        }
        priceResult.setPrice(newPayPrice);
        log.warn("九折 :");
        log.warn("deltaYears :" + deltaYears);
    }
}
