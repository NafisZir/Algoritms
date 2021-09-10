/**
 * Задание:
 * 1) Есть транспортные корабли, которые подплывают к туннели и далее плывут к причалам для
 *    погрузки разного рода товара.
 * 2) Они проходят через узкий туннель где одновременно могут находиться только 5 кораблей.
 *    Под словом “подплывают к туннели” имеется ввиду то, что корабли должны откуда-то
 *    появляться. Их может быть ограниченное количество, то есть 10 или 100, а может быть
 *    бесконечное множество. Слово “Подплывают” назовем генератором кораблей.
 * 3) Вид кораблей и их вместительность могут быть разными в зависимости от типа товаров,
 *    которые нужно загрузить на корабль. То есть для ТЗ я придумал 2 типа вместительности.
 * 4) Далее есть 2 вида причалов для погрузки кораблей. Каждый причал
 *    берет или подзывает к себе необходимый ему по вместительности корабль и начинает его загружать.
 *    За 2 секунды причал загружает на корабль 1 ед. товара. То есть если у корабля вместительность 5 шт.,
 *    то причал загрузит его за 5 секунд своей работы.
 */

import java.util.ArrayList;
import java.util.Random;

// This class simulates the tunnel
class Tunnel extends Thread{
    private Port port1;
    private Port port2;
    private Thread tPort1;
    private Thread tPort2;
    boolean isFinish = false;
    private static final int MAX_SHIPS = 5;  // Max quantity of ships in tunnel
    // These variables contain the capacity of ships that sail to the corresponding port
    private static final int capacityOfPort1 = 1;
    private static final int capacityOfPort2 = 2;
    private ArrayList<Integer> arrTunnel = new ArrayList<>();  // Will contain ships

    public Tunnel(){
        port1 = new Port(1);
        port2 = new Port(2);
        port1.setTunnel(this);
        port2.setTunnel(this);
    }

    public synchronized void shipEnter(int capacity){
        System.out.println("--Tunnel: new ship entered to tunnel--");
        if(capacity == capacityOfPort1) {
            arrTunnel.add(capacityOfPort1);
        } else {
            arrTunnel.add(capacityOfPort2);
        }

        // If tunnel is full we do "wait"
        if(arrTunnel.size() == MAX_SHIPS){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Port prompts the ship
    public synchronized void getShip(int capacity){
        for (int i = 0; i < arrTunnel.size(); i++) {
            if(capacity == capacityOfPort1) {
                if (arrTunnel.get(i) == capacityOfPort1) {
                    port1.shipEnter();
                    arrTunnel.remove(i);
                    notify();
                    break;
                }
            } else {
                if(arrTunnel.get(i) == capacityOfPort2){
                    port2.shipEnter();
                    arrTunnel.remove(i);
                    notify();
                    break;
                }
            }
        }
    }

    // The ships won't sail anymore. Therefore it's time to finish working
    private void finish(){
        port1.isFinish = true;
        port2.isFinish = true;

        // Wait finishing the work of ports
        try {
            tPort1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            tPort2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        interrupt();
    }

    @Override
    public void run() {
        tPort1 = new Thread(port1);
        tPort2 = new Thread(port2);
        tPort1.start();
        tPort2.start();

        while(!isInterrupted()) {
            if(isFinish && arrTunnel.size()==0){
                finish();
            }
        }
    }
}

// This class describes ports
class Port extends Thread{
    private Tunnel tunnel;
    private int capacity;
    private boolean isShip = false;
    boolean isFinish = false;

    public Port(int c){
        capacity = c;
    }

    // Gets an object of tunnel
    public void setTunnel(Tunnel tunnel) {
        this.tunnel = tunnel;
    }

    // The ship enters to this port
    public synchronized void shipEnter(){
        isShip = true;
    }

    // Loading the goods to the ship
    private synchronized void loading(){
        // If it's time to finish
        out:
        while(true) {
            while (!isShip) {
                if (isFinish) {
                    break out;
                }
                // Port prompt the ship if it's empty
                tunnel.getShip(capacity);
            }

            // Loading the goods
            System.out.println("Port " + capacity + " is starting loading");
            try {
                sleep(2000 * capacity);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isShip = false;
        }
    }

    @Override
    public void run() {
        loading();
    }
}

public class Task{
    public static void main(String[] args) {
        Tunnel tunnel = new Tunnel();
        Thread t1 = new Thread(tunnel);
        t1.start();

        int capacity;
        System.out.println("Start!");
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            capacity = random.nextInt(2) + 1;
            System.out.println("SEA: a new ship appeared on the horizon");
            tunnel.shipEnter(capacity);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        tunnel.isFinish = true;
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Finish!");
    }
}
