package com.sixthsense.hexastay.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sixthsense.hexastay.entity.QStoremenu;
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

    QStoremenu storemenu = QStoremenu.storemenu;


    @Override
    public Page<Store> storeTypeSearch(Long companyNum, String storeType, String menuKeyword, Pageable pageable) {
        BooleanExpression conditions = filteringConditions(companyNum,storeType);

        List<Store> content = queryFactory
                .selectDistinct(store)
                .join(storemenu).on(storemenu.store.eq(store))
                .leftJoin(store.company).fetchJoin()
                .where(
                        conditions,
                        menuKeyword!=null && !menuKeyword.isBlank() ? storemenu.storemenuName.contains(menuKeyword) : null
                        )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(store.storeNum.desc())
                .fetch();
        long total = queryFactory
                .select(store.countDistinct())
                .from(store)
                .join(storemenu).on(storemenu.store.eq(store))
                .where(
                        conditions,
                        menuKeyword!=null && !menuKeyword.isBlank() ? storemenu.storemenuName.contains(menuKeyword) : null
                )
                .fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression filteringConditions(Long companyNum, String storeType) {
        BooleanExpression condition = Expressions.TRUE;
        if(companyNum!=null&&companyNum!=0L){
            condition = condition.and(store.company.companyNum.eq(companyNum));
        }
        if(storeType!=null&&!storeType.isBlank()){
            condition = condition.and(store.storeCategory.eq(storeType));
        }
        return condition;
    }






    @Override
    public Page<Store> listStoreSearch(Long companyNum, String searchType, String keyword, Pageable pageable, String... status) {
        //조건
        BooleanExpression whereCondition = buildSearchCondition(companyNum, searchType, keyword, status);
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

    private BooleanExpression buildSearchCondition(Long companyNum, String searchType, String keyword, String... status) {
        BooleanExpression condition = Expressions.TRUE; // 기본 조건

        //스토어 활성화여부
        if(status!=null&&status.length>0){
            condition= condition.and(store.storeStatus.in(status));
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
