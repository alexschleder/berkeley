public class Clock extends Thread
{
    private long time;

    public Clock(long time)
    {
        this.time = time;
    }

    public void Run()
    {
        while(true)
        {
            try
            {
                Thread.sleep(1);
                time++;
            }
           
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    }  

    public long getTime()
    {
        return time;
    }

    public void addToTime(long offset)
    {
        time += offset;
    }
}