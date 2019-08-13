package Run;

import LogsParts.LogsT;

public class ClassRunAsyncSend implements Runnable{

    RunSend objSendSms = new RunSend();
    long id;

    public ClassRunAsyncSend(long id){

        this.id=id;
    }

    @Override
    public void run() {
        try {
            Thread ct = Thread.currentThread();
            //ct.setPriority(5);
            System.out.println(LogsT.printDate() + "run Thread: "+ct.getName());
            //System.out.println(LogsT.printDate() + "statusSending(" + this.id + ")");
            // убрал 27,08,2016 из-за массового перевода смс в статус sending и неотрпаки в дальнейшем смс
            //this.objSendSms.statusSending(this.id);
            System.out.println(LogsT.printDate() + "send(" + this.id + ")");
            System.out.println("!!! current thread in ClassRunAsyncSend.run before RunSend is "+Thread.currentThread().getName());
            this.objSendSms.send(this.id);
            System.out.println("!!! current thread in ClassRunAsyncSend.run after RunSend is " + Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
