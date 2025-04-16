package com.sixthsense.hexastay.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.sixthsense.hexastay.entity.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sixthsense.hexastay.entity.QAdmin.admin;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Admin> listAdminSearch(String select, String choice, String keyword, Pageable pageable) {

        // 조건
        BooleanExpression whereCondition = buildSearchCondition(select, choice, keyword);

        List<Admin> content = queryFactory
                .selectFrom(admin)
                .leftJoin(admin.company).fetchJoin()
                .leftJoin(admin.store).fetchJoin()
                .where(whereCondition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(admin.adminNum.desc()) // 원하는 정렬 조건
                .fetch();

        long total = queryFactory
                .select(admin.count())
                .from(admin)
                .where(whereCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression buildSearchCondition(String select, String choice, String keyword) {
        BooleanExpression condition = Expressions.TRUE; // 기본 조건

        // 회사 유형
        if (select != null && !"all".equalsIgnoreCase(select)) {
            condition = condition.and(admin.company.companyType.eq(select));
        }

        // 검색 필드
        if (keyword != null && !keyword.isBlank()) {
            switch (choice) {
                case "adminEmployeeNum":
                    condition = condition.and(admin.adminEmployeeNum.containsIgnoreCase(keyword));
                    break;
                case "adminRole":
                    condition = condition.and(admin.adminRole.containsIgnoreCase(keyword));
                    break;
                case "adminPosition":
                    condition = condition.and(admin.adminPosition.containsIgnoreCase(keyword));
                    break;
                case "adminName":
                    condition = condition.and(admin.adminName.containsIgnoreCase(keyword));
                    break;
            }
        }

        return condition;
    }
}
