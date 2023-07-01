package org.example;

import java.util.List;
import java.util.concurrent.Exchanger;

public class ExchangerExample {

    public static void main(String[] args) {
        // оба потока получат информацию друг от друга одновременно
        // точка синхронизации, где потоки обмениваются данными
        // оба потока ждут, пока получат друг от друга элементы
        Exchanger<Action> exchanger = new Exchanger<>();

        new User("me", List.of(Action.FIST, Action.SCISSORS), exchanger);
        new User("friend", List.of(Action.PAPER, Action.SCISSORS), exchanger);
    }
}

enum Action {
    FIST, SCISSORS, PAPER
}


class User extends Thread {

    private String name;
    private List<Action> myActions;
    private Exchanger<Action> exchanger;

    private void whoWins(Action myAction, Action friendsAction) {
        if (myAction == Action.FIST && friendsAction == Action.SCISSORS
                || myAction == Action.SCISSORS && friendsAction == Action.PAPER
                || myAction == Action.PAPER && friendsAction == Action.FIST) {
            System.out.println(name + ", you win!!!");
        } else {
            //System.out.println(name + ", you lose");
        }
    }

    public User(String name, List myActions, Exchanger exchanger) {
        this.name = name;
        this.exchanger = exchanger;
        this.myActions = myActions;
        this.start();
    }

    public void run() {

        for (Action action : myActions) {

            try {
                Action friendsReply = exchanger.exchange(action);

                whoWins(action, friendsReply);

                sleep(2000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);

            }
        }
    }
}
