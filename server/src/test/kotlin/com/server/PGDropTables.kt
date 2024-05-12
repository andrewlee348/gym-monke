package com.server

import bemonke.models.Post
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGSimpleDataSource
import kotlin.test.*

class PGDropTables {
    @Test
    fun test() {
        val config = loadConfigFromResources("config.json")
        val dataSource = PGSimpleDataSource()
        dataSource.setUrl(config.url)
        dataSource.setUser(config.user)
        dataSource.setPassword(config.pass)

        Database.connect(dataSource)
        transaction {
            SchemaUtils.drop(Users)
            SchemaUtils.drop(Posts)
        }
    }
}