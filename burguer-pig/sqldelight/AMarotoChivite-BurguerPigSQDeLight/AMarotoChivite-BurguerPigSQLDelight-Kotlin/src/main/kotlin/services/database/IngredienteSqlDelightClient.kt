package services.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import config.ConfigApp
import database.AppDatabase
import database.IngredienteQueries

private val logger = mu.KotlinLogging.logger { }

object IngredienteSqlDelightClient {

    lateinit var ingredienteQueries: IngredienteQueries
    private val connectionUrl = ConfigApp.getUrlDbConection()

    init {
        logger.debug { "${this::class.simpleName}: Inicializando el gestor de Bases de Datos" }

        initConfig()

        // Borramos la base de datos si está en la config=true
        if (ConfigApp.getDeleteDb()) {
            removeAllData()
        }
    }

    private fun initConfig() {

        val driver = JdbcSqliteDriver(connectionUrl)
        logger.debug { "${this::class.simpleName}: Creando Base de Datos" }
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)

        ingredienteQueries = database.ingredienteQueries
    }

    private fun removeAllData() {
        logger.debug { "${this::class.simpleName}: Eliminando todos los datos" }
        ingredienteQueries.transaction {
            ingredienteQueries.removeAll()
        }
    }
}