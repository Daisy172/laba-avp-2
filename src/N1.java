import java.io.*;

public class N1 extends FilterInputStream
{
    private final long bytesPerSecond;
    private long bytesRead;
    private long startTime;

    public N1(InputStream in, long bytesPerSecond)
    {
        super(in);
        this.bytesPerSecond = bytesPerSecond;
        this.bytesRead = 0;
        this.startTime = System.currentTimeMillis();
    }

    public static void main(String[] args)
    {
        try
        {
            String filePath = "src\\Demia.txt";

            long bytesPerSecond = 1024;
            InputStream fileInputStream = new FileInputStream(filePath);
            N1 bandwidthLimitedInputStream = new N1(fileInputStream, bytesPerSecond);

            int data;
            long startTime = System.currentTimeMillis();
            while ((data = bandwidthLimitedInputStream.read()) != -1)
            {
                System.out.print((char) data);
            }
            long endTime = System.currentTimeMillis();

            bandwidthLimitedInputStream.close();
            System.out.println("\nTime taken: " + (endTime - startTime) + " milliseconds");
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int read() throws IOException
    {
        throttle();
        int data = super.read();
        if (data != -1)
        {
            bytesRead++;
        }
        return data;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        throttle();
        int bytesRead = super.read(b, off, len);
        if (bytesRead != -1)
        {
            this.bytesRead += bytesRead;
        }
        return bytesRead;
    }

    private void throttle()
    {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        if (elapsedTime == 0)
        {
            return;
        }

        long expectedBytes = elapsedTime * bytesPerSecond / 1000;
        if (bytesRead > expectedBytes) {
            try
            {
                long sleepTime = (bytesRead - expectedBytes) * 1000 / bytesPerSecond;
                Thread.sleep(sleepTime);
            }

            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}