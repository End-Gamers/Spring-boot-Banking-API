package com.microfinanceBank.Issues.Service.Impl;


import com.microfinanceBank.Issues.entity.DbSequence;
import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.ReactiveMongoOperations;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

/**
 * MongoDB 기반 시퀀스 번호 생성 서비스.
 * findAndModify를 사용하여 원자적으로 시퀀스 값을 증가시킨다.
 */
@Component
@RequiredArgsConstructor
public class SequenceGeneratorService {
    private final ReactiveMongoOperations mongoOperations;

    /**
     * 지정된 시퀀스의 다음 번호를 반환한다.
     * 시퀀스가 없으면 자동 생성하고 1부터 시작한다.
     *
     * @param sequenceName 시퀀스 이름
     * @return 다음 시퀀스 번호
     */
    public Long getSequenceNumber(String sequenceName){
        Query query=new Query(Criteria.where("id").is(sequenceName));

        Update update=new Update().inc("seq",1);

        Mono<DbSequence> counter= mongoOperations
                .findAndModify(query,update,options().returnNew(true).upsert(true),DbSequence.class);

        return !Objects.isNull(counter) ?counter.block().getSeq() :1;
    }
}
