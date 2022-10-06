import java.io.*;

public class BitInputStream extends InputStream
{
    private InputStream     myInput;
    private int             myBitCount;
    private int             myBuffer;
    private File            myFile;

    private static final int bmask[] = {
            0x00, 0x01, 0x03, 0x07, 0x0f, 0x1f, 0x3f, 0x7f, 0xff,
            0x1ff,0x3ff,0x7ff,0xfff,0x1fff,0x3fff,0x7fff,0xffff,
            0x1ffff,0x3ffff,0x7ffff,0xfffff,0x1fffff,0x3fffff,
            0x7fffff,0xffffff,0x1ffffff,0x3ffffff,0x7ffffff,
            0xfffffff,0x1fffffff,0x3fffffff,0x7fffffff,0xffffffff
    };

    private static final int BITS_PER_BYTE = 8;

    public BitInputStream(String filename)
    {
        this(new File(filename));
    }

    public BitInputStream(File file)
    {
        myFile = file;
        try {
            reset();
        } catch (IOException e) {
            throw new RuntimeException("could not open file for reading bits "+e);
        }

    }

    public BitInputStream(InputStream in){
        myInput = in;
        myFile = null;
    }

    public boolean markSupported(){
        return myFile != null;
    }

    public void reset() throws IOException
    {
        if (! markSupported()){
            throw new IOException("not resettable");
        }
        try{
            close();
            myInput = new BufferedInputStream(new FileInputStream(myFile));
        }
        catch (FileNotFoundException fnf){
            System.err.println("error opening " + myFile.getName() + " " + fnf);
        }
        myBuffer = myBitCount = 0;
    }

    public void close()
    {
        try{
            if (myInput != null) {
                myInput.close();
            }
        }
        catch (java.io.IOException ioe){
            throw new RuntimeException("error closing bit stream " + ioe);
        }
    }

    public int readBits(int howManyBits) throws IOException
    {
        int retval = 0;
        if (myInput == null){
            return -1;
        }

        while (howManyBits > myBitCount){
            retval |= ( myBuffer << (howManyBits - myBitCount) );
            howManyBits -= myBitCount;
            try{
                if ( (myBuffer = myInput.read()) == -1) {
                    return -1;
                }
            }
            catch (IOException ioe) {
                throw new IOException("bitreading trouble "+ioe);
            }
            myBitCount = BITS_PER_BYTE;
        }

        if (howManyBits > 0){
            retval |= myBuffer >> (myBitCount - howManyBits);
            myBuffer &= bmask[myBitCount - howManyBits];
            myBitCount -= howManyBits;
        }
        return retval;
    }

    public int read() throws IOException {
        return readBits(8);
    }
}