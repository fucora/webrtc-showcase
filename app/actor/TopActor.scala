package actor

import javax.inject.Inject

import akka.actor._
import play.api.Configuration

import scala.collection.mutable


case class GetRoomActor(room: String)

/**
  * Created by Stan Reshetnyk on 23.03.17.
  */
class TopActor @Inject() (configuration: Configuration) extends BaseActor with ActorLogging {

  val roomActorRefs = new mutable.HashMap[String, ActorRef]()

  override def receive: Receive = {
    case GetRoomActor(room) =>
      val roomActor = roomActorRefs.get(room).getOrElse(createRoomActor(room))
      sender ! roomActor

    case Terminated(child) => log.info("Terminated actor " + child.path.name)

    case a: Any => log.info("Unhandled message " + a)
  }

  def createRoomActor(room: String): ActorRef = {
    val props = Props(new MeetingActor(room, configuration))
    val roomActor = context.actorOf(props, name = "room-" + room)
    roomActorRefs.put(room, roomActor)
    context.watch(roomActor)
    roomActor
  }
}
