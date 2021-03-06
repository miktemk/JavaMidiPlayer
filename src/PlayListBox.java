import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JComboBox;

/*
 * Created on Oct 22, 2005
 *
 */

/**
 * @author Mikhail Temkine
 *
 */
public class PlayListBox extends JComboBox
{
    private class MyListener implements ActionListener
    {
        MIDIPlayer pl;
        PlayingDevice p;
        JComboBox box;
        Checker checker;
        int i;
        /**
         * @param p - the PlayingDevice object
         */
        public MyListener(MIDIPlayer pl, PlayingDevice p, JComboBox box, Checker checker)
        {
            this.pl = pl;
            this.p = p;
            this.box = box;
            this.checker = checker;
            i = box.getSelectedIndex();
        }
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            int j = box.getSelectedIndex();
            if(i != j)
            {
                i = j;
                pl.playSong(i);
            }
        }
        
    }
    public PlayListBox(Vector list, MIDIPlayer p)
    {
        super(list);
        addActionListener(new MyListener(p, p.p, this, p.checker));
    }
}