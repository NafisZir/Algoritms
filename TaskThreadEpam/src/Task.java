import java.util.ArrayList;
import java.util.Random;

class Tunnel extends Thread{
    ArrayList<Integer> arr = new ArrayList<>();
    Port port1;
    Port port2;
    boolean isFinish = false;

    public Tunnel(Port p1, Port p2){
        port1 = p1;
        port2 = p2;
    }

    public synchronized void shipEnter(int number){
        if(number == 1) {
            arr.add(1);
        } else {
            arr.add(2);
        }

        if(arr.size() == 5){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Got ship!");
    }

    public synchronized void getShip1(){
        for (int i = 0; i < arr.size(); i++) {
            if(arr.get(i) == 1){
                port1.shipEnter();
                arr.remove(i);
                notify();
                break;
            }
        }
    }

    public synchronized void getShip2(){
        for (int i = 0; i < arr.size(); i++) {
            if(arr.get(i) == 2){
                port2.shipEnter();
                arr.remove(i);
                notify();
                break;
            }
        }
    }

    private void finish(){
        port1.isFinish = true;
        port2.isFinish = true;

        try {
            port1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            port2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        interrupt();
    }

    @Override
    public void run() {
        while(!isInterrupted()) {
            if(isFinish && arr.size()==0){
                finish();
            }
        }
    }
}

class Port extends Thread{
    int scope;
    boolean isShip = false;
    boolean isFinish = false;
    Tunnel tunnel;

    public Port(int s){
        scope = s;
    }

    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    public synchronized void shipEnter(){
        isShip = true;
    }

    private synchronized void loading(){
        out:
        while(true) {
            while (!isShip) {
                if (isFinish) {
                    break out;
                } else if (scope == 1) {
                    tunnel.getShip1();
                } else {
                    tunnel.getShip2();
                }
            }

            try {
                sleep(50 * scope);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("---------------" + scope);

            notify();
            isShip = false;
        }
    }

    @Override
    public void run() {
        loading();
    }
}

public class Task{
    static Port port1;
    static Port port2;
    static Tunnel tunnel;

    public static void main(String[] args) {
        port1 = new Port(1);
        port2 = new Port(2);
        tunnel = new Tunnel(port1, port2);
        port1.setTunnel(tunnel);
        port2.setTunnel(tunnel);

        Thread t1 = new Thread(tunnel);
        Thread t2 = new Thread(port1);
        Thread t3 = new Thread(port2);

        t1.start();
        t2.start();
        t3.start();

        int r;
        System.out.println("Start!");
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println("Made 1 ship");

            r = random.nextInt(2) + 1;
            tunnel.shipEnter(r);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tunnel.isFinish = true;
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("haha");
        }
        System.out.println("Finish!");
    }
}
