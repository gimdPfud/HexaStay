package com.sixthsense.hexastay.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sixthsense.hexastay.entity.Store;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sixthsense.hexastay.entity.QStore.store;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Store> listStoreSearch(String status, Long companyNum, String searchType, String keyword, Pageable pageable) {
        //조건
        BooleanExpression whereCondition = buildSearchCondition(status, companyNum, searchType, keyword);
        List<Store> content = queryFactory
                .selectFrom(store)
                .leftJoin(store.company).fetchJoin()
                .where(whereCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(store.storeNum.desc())
                .fetch();
        long total = queryFactory
                .select(store.count())
                .from(store)
                .where(whereCondition)
                .fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression buildSearchCondition(String status, Long companyNum, String searchType, String keyword) {
        BooleanExpression condition = Expressions.TRUE; // 기본 조건

        //스토어 활성화여부
        if(status!=null&&!status.isBlank()){
            condition= condition.and(store.storeStatus.eq(status));
        }

        // 회사 유형
        if (companyNum != null && companyNum!=0L) {
            condition = condition.and(store.company.companyNum.eq(companyNum));
        }

        // 검색 필드
        if (keyword != null && !keyword.isBlank()) {
            switch (searchType) {
                case "storeName" :
                    condition = condition.and(store.storeName.contains(keyword));
                    break;
                case "storeAddress" :
                    condition = condition.and(store.storeAddress.contains(keyword));
                    break;
                case "storeCeoName" :
                    condition = condition.and(store.storeCeoName.contains(keyword));
                    break;
            }
        }
        return condition;
    }


}
