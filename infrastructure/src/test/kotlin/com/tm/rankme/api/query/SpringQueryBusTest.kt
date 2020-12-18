package com.tm.rankme.api.query

import com.tm.rankme.cqrs.query.Query
import com.tm.rankme.cqrs.query.QueryBus
import com.tm.rankme.cqrs.query.QueryHandler
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component


@SpringBootTest(classes = [SpringQueryBus::class, TestQueryHandler::class])
internal class SpringQueryBusTest(@Autowired private val queryBus: QueryBus) {
    @Test
    internal fun `Should execute query`() {
        // given
        val testValue = "Test value"
        val query: Query = TestQuery(testValue)
        // when
        val result = queryBus.execute<TestModel>(query)
        // then
        assertEquals(testValue, result.testValue)
    }

    @Test
    internal fun `Should throw exception when cannot find handler for query`() {
        // given
        val query: Query = QueryWithoutHandler("Query value")
        // when
        val exception = assertFailsWith<IllegalStateException> { queryBus.execute(query) }
        // then
        assertEquals("Query type not found", exception.message)
    }
}

@Component
private class TestQueryHandler : QueryHandler<TestQuery, TestModel> {
    override fun handle(query: TestQuery): TestModel {
        return TestModel(query.testProperty)
    }
}

private data class TestQuery(var testProperty: String) : Query()
private data class TestModel(var testValue: String)
private data class QueryWithoutHandler(var testProperty: String) : Query()
