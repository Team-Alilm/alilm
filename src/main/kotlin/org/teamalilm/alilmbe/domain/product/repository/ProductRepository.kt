package org.teamalilm.alilmbe.domain.product.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.domain.product.entity.Product
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo

interface ProductRepository : JpaRepository<Product, Long> {

    fun findByProductInfo(productInfo: ProductInfo): Product?
    fun findAllByIsDeleteFalse(pageRequest: PageRequest): Slice<Product>
}