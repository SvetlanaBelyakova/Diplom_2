package yandex.praktikum;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

// Конструктор с параметрами
public class Order {
    private List<String> ingredients;

    // Конструктор без параметров
    public Order() {
        ingredients = new ArrayList<>();
    }

    // Геттер для ingredients
    public List<String> getIngredients() {
        return ingredients;
    }

    // Сеттер для ingredients
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // Переопределение метода toString
    @Override
    public String toString() {
        return "Order{" +
                "ingredients=" + ingredients +
                '}';
    }

    // Переопределение метода equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;

        Order order = (Order) o;

        return Objects.equals(ingredients, order.ingredients);
    }

    // Переопределение метода hashCode
    @Override
    public int hashCode() {
        return ingredients != null ? ingredients.hashCode() : 0;
    }
}