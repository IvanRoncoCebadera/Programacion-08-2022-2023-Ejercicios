package repositories

import config.AppConflig
import models.Coche
import models.TipoMotor
import mu.KotlinLogging
import java.sql.DriverManager
import java.sql.Statement

object CocheRepository {
    private val logger = KotlinLogging.logger {}
    private val connection = DriverManager.getConnection(AppConflig.databaseUrl)

    init {
        var sql = """CREATE TABLE IF NOT EXISTS coches 
        (id     INTEGER  PRIMARY KEY AUTOINCREMENT,
         marca  TEXT     NOT NULL,
         modelo TEXT     NOT NULL,
         precio DOUBLE   NOT NULL,
         motor  TEXT     NOT NULL)
    """.trimIndent()
        connection.createStatement().use { statement ->
            statement.executeUpdate(sql).also { println("Table created") }
        }
    }

    fun findAll(): List<Coche> {
        logger.debug { "findAll  REPOSITORIO" }
        var sql = "SELECT * FROM coches"
        connection.createStatement().use { statement ->
            statement.executeQuery(sql).use { resultSet ->
                val coches = mutableListOf<Coche>()
                while (resultSet.next()) {
                    coches.add(
                        Coche(
                            id = resultSet.getLong("id"),
                            marca = resultSet.getString("marca"),
                            modelo = resultSet.getString("modelo"),
                            precio = resultSet.getDouble("precio"),
                            motor = TipoMotor.valueOf(resultSet.getString("motor"))
                        )
                    )
                }
                return coches
            }
        }
    }

    fun findById(id: Long): Coche? {
        var sql = "SELECT * FROM coches WHERE id = ?"
        connection.prepareStatement(sql).use { statement ->
            // Le paso los parámetros
            statement.setLong(1, id)
            statement.executeQuery().use { resultSet ->
                if (resultSet.next()) {
                    return Coche(
                        id = resultSet.getLong("id"),
                        marca = resultSet.getString("marca"),
                        modelo = resultSet.getString("modelo"),
                        precio = resultSet.getDouble("precio"),
                        motor = TipoMotor.valueOf(resultSet.getString("motor"))
                    )
                }
            }
        }
        return null
    }

    fun save(entity: Coche): Coche {
        var sql = "INSERT INTO coches (marca,modelo,precio,motor) VALUES (?,?,?,?)"
        var myId = -99L

        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            .use { statement ->
                // Le paso los parámetros
                statement.setString(1, entity.marca)
                statement.setString(2, entity.modelo)
                statement.setDouble(3, entity.precio)
                statement.setString(4, entity.motor.name)
                val res = statement.executeUpdate()
                // Ahora voy a por la clave generada
                val rs = statement.generatedKeys
                if (rs.next()) {
                    myId = rs.getLong(1)
                }
            }
        return entity.copy(id = myId)
    }

    fun update(entity: Coche): Coche? {
        var sql = "UPDATE coches SET marca = ?, modelo = ?, precio = ?, motor = ? WHERE id = ${entity.id}"

        connection.prepareStatement(sql).use { statement ->
            // Le paso los parámetros
            statement.setString(1, entity.marca)
            statement.setString(2, entity.modelo)
            statement.setDouble(3, entity.precio)
            statement.setString(4, entity.motor.name)
            val res = statement.executeUpdate()
            if (res == 1) return entity
        }
        return null
    }

    fun deleteById(id: Long): Boolean {
        var sql = "DELETE FROM coches WHERE id = ?"

        connection.prepareStatement(sql).use { statement ->
            // Le paso los parámetros
            statement.setLong(1, id)
            val res = statement.executeUpdate()
            println("Borrado $res filas")
            if (res == 1) return true
        }
        return false
    }
}
