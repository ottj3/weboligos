package services

import javax.inject.{Inject, Singleton}

import model.OligoJob
import play.api.db.Database

import scala.collection.mutable

@Singleton
class DBQueries @Inject()(db: Database) {

  var store: mutable.HashMap[Long, OligoJob] = mutable.HashMap.empty

  def store(job: OligoJob): Unit = {
    store.put(job.id, job)
  }

  def get(jobId: Long): Option[OligoJob] = {
    store.get(jobId)
  }
}
