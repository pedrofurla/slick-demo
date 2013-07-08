package slickdemo.model

import slickdemo.dal._
import DAL._
import dataLayer.profile.simple._

case class HotSite(id: PK, url: String, pageTitle: String, title: String, login: String, password: String,
                   branchId: Int, disabled: Boolean = false) {
  //lazy val branch = withSession { HotSites.branch(id.get).first()(_) }
  lazy val logoName = s"logo-$url.png"

  /*def disable(value: Boolean = true) = inSession {
    val original = HotSites.byId(id.get)
    val result = copy(disabled = value)
    original.update(result)
    result
  }*/
}

object HotSite {
  def allEnabled = withSession { (for (entity <- HotSites if !entity.disabled) yield entity).list()(_) }
}