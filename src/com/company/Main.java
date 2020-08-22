package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import static com.company.Main.EOF;
public class Main {
    public static final String EOF = "EOF";
    public static void main(String[] args) {
        List<String> buffer = new ArrayList<>();
        ReentrantLock reentrantLock = new ReentrantLock();
        MyProducer myProducer = new MyProducer(buffer,ThreadColor.ANSI_CYAN,reentrantLock);
        MyConsumer myConsumer1 = new MyConsumer(buffer,ThreadColor.ANSI_PURPLE,reentrantLock);
        MyConsumer myConsumer2 = new MyConsumer(buffer,ThreadColor.ANSI_GREEN,reentrantLock);

        new Thread(myProducer).start();
        new Thread(myConsumer1).start();
        new Thread(myConsumer2).start();

    }
}

class MyProducer implements Runnable{
    private List<String> buffer;
    private String color;
    private ReentrantLock reentrantLock;

    public MyProducer(List<String> buffer, String color, ReentrantLock reentrantLock) {
        this.buffer = buffer;
        this.color = color;
        this.reentrantLock = reentrantLock;
    }
    public void run(){
        Random random = new Random();
        String[] number = {"1","2","3","4","5"};
        for (String num : number){
            try {
                System.out.println(color + "adding "+num);
                try{
                    reentrantLock.lock();
                    buffer.add(num);
                }finally {
                    reentrantLock.unlock();
                }

                Thread.sleep(random.nextInt(1000));
            }catch ( InterruptedException e ){
                System.out.println("Producer was interrupted");
            }
        }
        System.out.println(color + "adding EOF and exiting...");
        try{
            reentrantLock.lock();
            buffer.add("EOF");
        }finally {
            reentrantLock.unlock();
        }

    }
}


class MyConsumer implements Runnable{
    private List<String> buffer;
    private String color;
    private ReentrantLock reentrantLock;

    public MyConsumer(List<String> buffer, String color, ReentrantLock reentrantLock) {
        this.buffer = buffer;
        this.color = color;
        this.reentrantLock = reentrantLock;
    }
    public void run(){
        while (true){
             int counter =0;
            if (reentrantLock.tryLock()){
                try{
                    if (buffer.isEmpty()){
                        continue;
                    }
                    System.out.println(counter+ "counter "+counter);
                    counter =0;
                    if (buffer.get(0).equals(EOF)){
                        System.out.println(color +" exiting...");
                        break;
                    }else {
                        System.out.println(color + "consuming : "+ buffer.remove(0));
                    }
                }finally {
                    reentrantLock.unlock();
                }
            }else {
                counter++;
            }
        }
    }
}