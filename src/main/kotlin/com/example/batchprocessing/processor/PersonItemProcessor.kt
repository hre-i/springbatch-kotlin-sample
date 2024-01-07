package com.example.batchprocessing.processor

import com.example.batchprocessing.model.Person
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.batch.item.ItemProcessor
import java.util.Locale

/**
 * ItemProcessor のサンプル
 */
class PersonItemProcessor : ItemProcessor<Person, Person> {
    /**
     * [person] の firstName と lastName をすべて大文字に変換したものを返す
     */
    override fun process(person: Person): Person {
        val firstName = person.firstName!!.uppercase(Locale.getDefault())
        val lastName = person.lastName!!.uppercase(Locale.getDefault())

        val transformedPerson = Person(firstName, lastName)

        log.info("Converting ($person) into ($transformedPerson)")

        return transformedPerson
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PersonItemProcessor::class.java)
    }
}
