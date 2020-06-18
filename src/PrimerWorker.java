import akka.actor.AbstractActor;

import java.util.ArrayList;

public class PrimerWorker extends AbstractActor {
    int id;

    public PrimerWorker(int id) {
        this.id = id;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().matchAny(this::onReceive).build();
    }

    private void onReceive(Object message) {
        System.out.println("Got message in worker : " + id + " message: " + message);

        if(message instanceof int[]) {
            int[] rangeArray = (int[]) message;

            int from = rangeArray[0];
            int to = rangeArray[1];

            for(int i = from; i < to; i++) {
                if(isPrime(i)) {
                    System.out.println(i);
                }
            }
        }
    }

    private boolean isPrime(int num) {
        if(num == 1) {
            return false;
        }
        if (num == 2) {
            return false;
        }
        if (num% 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= num; i+=2) {
            if(num % i == 0) {
                return false;
            }
        }

        return true;
     }
}
