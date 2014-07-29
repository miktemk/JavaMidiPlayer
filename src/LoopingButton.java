/*
 * Created on Oct 22, 2005
 *
 */

/**
 * @author Mikhail Temkine
 *
 * Options button button
 */
public class LoopingButton extends ControlButton
{
    MIDIPlayer p;
    LoopMenu m;
    public LoopingButton(MIDIPlayer p, int selIndex)
    {
        super("img/options1_up.png", "img/options1_over.png", "img/options1_down.png");
        this.p = p;
        m = new LoopMenu(p, selIndex);
    }
    public void doAction()
    {
        m.show(this, 0, getHeight());
    }
}
