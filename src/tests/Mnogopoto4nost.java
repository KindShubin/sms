package tests;

class Mnogopoto4nost {

    public Mnogopoto4nost(){
        int c = 0;
        while(c<5){
            int value = 888888888;
            System.out.println("Запускаемсчетчик. c="+c);
            Runnable r = new NewThread(value+c);
            Thread t = new Thread(r);
            t.start();
            System.out.println("Пока выполняется циклсчетчика – Выведем это сообщение. c="+c);
            System.out.println("Ну и наверно посчитаем значение Pi в квадрате: " + Math.PI * Math.PI + ". c="+c);
            c++;
        }
    }
}

class NewThread implements Runnable {

    private int i = 999999999;
    public NewThread() {
    }

    public NewThread(int i){
        this.i=i;
    }

    public void run() {
        long num = 0;
        while (num < this.i) {
            num++;
        }
        System.out.println("Результат работы счетчика: " + num);
    }
}

class Main {

    public static void main(String args[]) {
        Mnogopoto4nost mn = new Mnogopoto4nost();
    }
}

