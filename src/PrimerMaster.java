import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

import java.util.ArrayList;
import java.util.List;

public class PrimerMaster extends AbstractActor {

    private int numOfRoutees;
    private Router workerRouter;

    private List<Routee> routees = new ArrayList<Routee>();

    public PrimerMaster(int divide) {
        this.numOfRoutees = divide;
        System.out.println("Number of chunks: " + divide);

        for(int i = 0; i < divide; i++) {
            ActorRef workerActor = getContext().actorOf(Props.create(PrimerWorker.class, i));
            routees.add(new ActorRefRoutee(workerActor));
        }

        workerRouter = new Router(new RoundRobinRoutingLogic(), routees);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(this::onReceive).build();
    }

    private void onReceive(Object message) {
        System.out.println("Called Primer Master, " + message);

        if(message instanceof int[]) {
            int[] range = (int[]) message;

            var from = range[0];
            var to = range[1];

            System.out.println("From: " + from + " To: "+ to);

            int totalRange = to-from; //in this case 1000

            int rangeLength = totalRange / numOfRoutees; //each range will have 50 numbers

            for(int i = 0; i < numOfRoutees; i++) {
                int subFrom = from + (i * rangeLength);

                int subTo = subFrom + rangeLength-1; //0-49

                if(i == numOfRoutees) {
                    subTo = to;
                }
                int[] send = {subFrom, subTo};

                workerRouter.route(send, getSelf());
            }

            workerRouter.route(" Hello Worker", getSelf());
//            workerRouter.route(" Hello Worker", getSelf());
        }
    }
}
