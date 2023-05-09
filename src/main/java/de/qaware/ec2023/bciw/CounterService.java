package de.qaware.ec2023.bciw;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Getter
public class CounterService {
    private int counter = 0;
    private final List<Runnable> listeners = new ArrayList<>();

    public void increment() {
        this.counter++;
        notifyListeners();
    }

    public void decrement() {
        this.counter--;
        notifyListeners();
    }

    public void addListener(Runnable listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
