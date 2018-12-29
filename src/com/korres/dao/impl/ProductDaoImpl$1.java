package com.korres.dao.impl;

import java.util.Comparator;
import com.korres.entity.SpecificationValue;
import org.apache.commons.lang.builder.CompareToBuilder;

class ProductDaoImpl$1 implements Comparator<SpecificationValue> {
	ProductDaoImpl$1(ProductDaoImpl paramProductDaoImpl) {
	}

	public int compare(SpecificationValue a1, SpecificationValue a2) {
		return new CompareToBuilder().append(a1.getSpecification(),
				a2.getSpecification()).toComparison();
	}
}