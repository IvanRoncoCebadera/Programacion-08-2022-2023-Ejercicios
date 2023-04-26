package service.database.profesores

import config.ConfigApp
import mu.KotlinLogging
import java.sql.DriverManager

object ProfesoresDatabase {

    val config = ConfigApp

    val logger = KotlinLogging.logger {  }

    val connection get() = DriverManager.getConnection(config.APP_URL)

    init {

        connection.use {
            it.prepareStatement("DROP TABLE IF EXISTS PROFESORES").use {stm ->
                stm.executeUpdate()
            }
        }

        val sqlCreateTable =
            """
                CREATE TABLE PROFESORES(
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                UUID TEXT UNIQUE NOT NULL,
                NOMBRE TEXT NOT NULL,
                EXPERIENCIA INTEGER NOT NULL
                )""".trimIndent()
        connection.use {
            it.prepareStatement(sqlCreateTable).use {stm ->
                stm.executeUpdate()
            }
        }
    }
}