/*
 * Created on Oct 22, 2005
 *
 */

/**
 * @author Mikhail Temkine
 *
 * "Play" button
 */
public class StopButton extends PlayerButton
{
    public StopButton(PlayingDevice p)
    {
        super(p, "img/stop_up.png", "img/stop_over.png", "img/stop_down.png");
        
    }
    public void doAction()
    {
        p.stop();
    }
}

