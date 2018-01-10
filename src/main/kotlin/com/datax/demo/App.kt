package com.datax.demo

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.datastax.driver.core.exceptions.AlreadyExistsException

fun main(args: Array<String>) {

    val cluster = Cluster.builder()
        .addContactPoint("127.0.0.1")
        .build()

    val session = initSession(cluster.connect())
    val db = TrackerDatabase(session)

    db.addMessage("peter", "hello sb!")
    db.addMessage("lucy", "fuck you!!")

    val msgList = db.getMessages()
    msgList.forEach { msgInfo -> println(msgInfo) }

}

fun initSession(session: Session): Session {
    // Datastax java driver provides no means to create table schemas from ORM defined tables...
    try {
        session.execute("CREATE KEYSPACE netdisk WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 }")
        session.execute("USE netdisk")
    } catch (e: AlreadyExistsException) {}
    try {
        session.execute("""CREATE TABLE location (
                object_id varchar,
                event_time timestamp,
                latitude float,
                longitude float,
                velocity float,
                PRIMARY KEY (object_id, event_time))""")
    } catch (e: AlreadyExistsException) {}
    try {
        session.execute("""CREATE TABLE chat(
                message_id varchar,
                event_time timestamp,
                message varchar,
                PRIMARY KEY (message_id, event_time))""")
    } catch (e: AlreadyExistsException) {}
    return session
}