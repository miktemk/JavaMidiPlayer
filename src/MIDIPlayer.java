import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.sound.midi.Sequence;
import javax.swing.*;

/*
 * Created on Oct 22, 2005
 *
 */

/**
 * @author Mikhail Temkine
 * 
 * This class handles all the GUI and events in it
 * 
 */
public class MIDIPlayer extends JFrame
{
    public PlayingDevice p;
    public Vector playlist;
    public PlayListBox listbox;
    public Checker checker;
    public SongLooper sl;
    public PositSlider slid;
    public AdvancedControls ac;
    private Vector<MIDIPlayerListener> events;
    private boolean acShow = false,
                    acActive = true;
    public MIDIPlayer()
    {
        super("Java MIDI Player");
        initialize(4); //init with the RANDOM looping style (i.e. 4)
    }
    public MIDIPlayer(String[] args)
    {
        super("Java MIDI Player");
        initialize(Integer.parseInt(args[0]));
        for(int i = 1; i < args.length; i++)
            loadTitle(new File(args[i]));
        listbox.updateUI();
        listbox.setSelectedItem(0);
        playSong(playlist.size());
    }
    private void initialize(/*boolean autoload, */int selIndex)
    {
        p = new PlayingDevice();
        playlist = new Vector();
        events = new Vector<MIDIPlayerListener>();
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                p.close();
                System.exit(0);
            }
            public void windowIconified(WindowEvent arg0)
            {
                ac.setVisible(false);
            }
            public void windowDeiconified(WindowEvent arg0)
            {
                ac.setVisible(acShow);
            }
            public void windowActivated(WindowEvent e)
            {
                ac.setVisible(acShow);
            }
        });
        addComponentListener(new ComponentAdapter()
        {
            public void componentMoved(ComponentEvent arg0)
            {
                Point location = getLocation();
                ac.setLocation(location.x+(getWidth()-ac.getWidth())/2, location.y+getHeight());
            }
        });
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        slid = new PositSlider(p.getSequencer(), this);
        checker = new Checker(p.getSequencer(), slid);
        
        JPanel buttnz = new JPanel(new FlowLayout());
        buttnz.add(new AdvancedButton(this));
        buttnz.add(new PlayButton(p));
        buttnz.add(new PauseButton(p));
        buttnz.add(new StopButton(p));
        listbox = new PlayListBox(playlist, this);
        JPanel corner = new JPanel(new FlowLayout());
        
        corner.add(new BrowseButton(this));
        corner.add(new LoopingButton(this, selIndex));
        
        JPanel middle = new JPanel(new BorderLayout());
        middle.add(buttnz, BorderLayout.WEST);
        middle.add(listbox, BorderLayout.CENTER);
        middle.add(corner, BorderLayout.EAST);
        
        //JPanel bottom = new JPanel(new BorderLayout());
        //bottom.add(new JSlider());
        
        pane.add(slid, BorderLayout.NORTH);
        pane.add(middle, BorderLayout.CENTER);
        //pane.add(bottom, BorderLayout.SOUTH);
        
        ac = new AdvancedControls(this);
        
        setSize(700, 98);
        setResizable(false);
        setVisible(true);
        
        checker.start();
        
        /*if(autoload)
        {
            File ff = new File("C:/Documents and Settings/Mikhail Temkine/My Documents/My Music/beatles MIDI");
            File[] ffs = ff.listFiles();
            for(int i = 0; i < ffs.length; i++)
            {
                addSong(ffs[i]);
            }
        }*/
    }
    public void loadTitle(File f)
    {
        
        if(f.isDirectory())
        {
            File[] ffs = f.listFiles();
            for(int i = 0; i < ffs.length; i++)
            {
                loadTitle(ffs[i]);
            }
        }
        else
        {
            SequenceItem si = new SequenceItem(f);
            playlist.addElement(si);
        }
    }
    public void addSong(File f)
    {
        try
        {
            SequenceItem item = null;
            for(Enumeration e = playlist.elements(); e.hasMoreElements();)
            {
                SequenceItem cur = (SequenceItem)e.nextElement();
                if(cur.getFile().equals(f))
                {
                    item = cur;
                    break;
                }
            }
            if(item != null)
            {
                //switch index
                listbox.setSelectedItem(item);
                for(Iterator<MIDIPlayerListener> i = events.iterator(); i.hasNext();)
                {
                    MIDIPlayerListener cur = i.next();
                    cur.songChanged(this);
                }
            }
            else
            {
                //load up
                //Sequence seq = PlayingDevice.loadSequence(f);
                //if(seq == null)
                //    return;
                SequenceItem si = new SequenceItem(f);
                playlist.addElement(si);
                listbox.updateUI();
                listbox.setSelectedItem(si);
                Sequence seq = PlayingDevice.loadSequence(f);
                checker.setSong(seq.getTickLength());
                p.setSequence(seq);
                for(Iterator<MIDIPlayerListener> i = events.iterator(); i.hasNext();)
                {
                    MIDIPlayerListener cur = i.next();
                    cur.songChanged(this);
                }
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, new JLabel("Error loading MIDI file, "+f.getName()), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void songEnded()
    {
        p.stop();
        sl.nextSong();
    }
    public void playSong(int index)
    {
        SequenceItem newItem = (SequenceItem)playlist.elementAt(index % playlist.size());
        listbox.setSelectedItem(newItem);
        SequenceItem item = (SequenceItem)listbox.getSelectedItem();
        Sequence seq = null;
        try
        {
            seq = PlayingDevice.loadSequence(item.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, new JLabel("Error loading MIDI file, "+item.getFile().getName()), "File Error", JOptionPane.ERROR_MESSAGE);
        }
        if(seq == null)
            return;
        checker.setSong(seq.getTickLength());
        p.setSequence(seq);
        for(Iterator<MIDIPlayerListener> i = events.iterator(); i.hasNext();)
        {
            MIDIPlayerListener cur = i.next();
            cur.songChanged(this);
        }
    }
    public void slideAdvanced(boolean show)
    {
        acShow = show;
        Point location = getLocation();
        ac.setLocation(location.x+(getWidth()-ac.getWidth())/2, location.y+getHeight());
        ac.setVisible(show);
    }
    //------------------------------------
    public void setSongLooper(SongLooper sl)
    {
        this.sl = sl;
    }
    public void addPlayerListener(MIDIPlayerListener lll)
    {
        events.add(lll);
    }
    public SequenceItem getCurrentSongFile()
    {
        return (SequenceItem)listbox.getSelectedItem();
    }
    public Sequence getCurrentSequence()
    {
        return p.getSequence();
    }
    
    public static void main(String[] args)
    {
        if(args.length > 0)
            new MIDIPlayer(args);
        else
            new MIDIPlayer();
    }
}
