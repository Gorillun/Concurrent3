/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project4;

import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author keith
 */
public class Sweep extends RecursiveAction {

    private ConcurrentLinkedQueue<Sweep> que;
    private ArrayList<RecursiveAction> jobs;
    private JPanel panel;
    private JButton[] buttons;
    private final int MAX_THRESHOLD = 2;
    private int start;
    private int stop;
    private int sum = 0;
    volatile int result;
    volatile int marbles;
    volatile boolean moves = false;

    Sweep(JButton[] buttons, int start, int stop, JPanel panel) {
        this.buttons = buttons;
        this.start = start;
        this.stop = stop;
        this.panel = panel;
        que = new ConcurrentLinkedQueue();
        moves =false;

    }

    @Override
    protected void compute() {

        if (stop - start < MAX_THRESHOLD) {

            for (int i = start; i < stop; ++i) {

                ++result;
                int x = buttons[i].getX();
                int y = buttons[i].getY();
                JButton leaped;
                JButton land;
                if (!(x + 160 > 640)) {

                    leaped = (JButton) panel.getComponentAt(x + 80, y);
                    land = (JButton) panel.getComponentAt(x + 160, y);
                    if (land.getName().equals("b") && leaped.getName().equals("m")) {
                        JButton[] temp = new JButton[2];;
                        temp[0] = leaped;
                        temp[1] = land;
                        moves = true;
                    }
                }
                if (!(x - 160 < 0)) {

                    leaped = (JButton) panel.getComponentAt(x - 80, y);
                    land = (JButton) panel.getComponentAt(x - 160, y);
                    if (land.getName().equals("b") && leaped.getName().equals("m")) {
                        JButton[] temp = new JButton[2];;
                        temp[0] = leaped;
                        temp[1] = land;
                        moves = true;
                    }
                }
                if (!(y + 160 > 640)) {

                    leaped = (JButton) panel.getComponentAt(x, y + 80);
                    land = (JButton) panel.getComponentAt(x, y + 160);
                    if (land.getName().equals("b") && leaped.getName().equals("m")) {

                        JButton[] temp = new JButton[2];;
                        temp[0] = leaped;
                        temp[1] = land;
                        moves = true;
                    }
                }
                if (!(y - 160 < 0)) {

                    leaped = (JButton) panel.getComponentAt(x, y - 80);
                    land = (JButton) panel.getComponentAt(x, y - 160);
                    if (land.getName().equals("b") && leaped.getName().equals("m")) {
                        JButton[] temp = new JButton[2];;
                        temp[0] = leaped;
                        temp[1] = land;
                        moves = true;
                    }
                }

            }
        } else {

            int mid = (start + stop) / 2;
            Sweep left = new Sweep(buttons, start, mid, panel);
            Sweep right = new Sweep(buttons, mid, stop, panel);
            right.fork();
            left.fork();
            left.join();
            right.join();
            result = left.result + right.result;

            if (left.moves || right.moves) {
                moves = true;
            }
        }

    }

    public int getResult() {
        if (!isDone()) {
            throw new Error("not Done");
        }

        return result;
    }

    public boolean getMove() {
        if (!isDone()) {
            throw new Error("not Done");
        }
        return moves;
    }

}
