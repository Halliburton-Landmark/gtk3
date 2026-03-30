

import java.awt.Frame;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * Drag "Source" button onto "Swing Target" button.
 * Repeat several times.
 * 
 * Expected behavior: with patched GTK, JVM shouldn't crash.
 * Not patched GTK causes JVM to crash in 5 minutes.
 * 
 * Note, if Swing button is not displayed then make sure you log in into X11 session and not Wayland.
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=551303
 *
 */
public class DndCrashIssue {

	public static void main(String[] args) {

		Shell shell = new Shell(SWT.DIALOG_TRIM);
		System.out.println("GTK version: " + System.getProperty("org.eclipse.swt.internal.gtk.version"));
		shell.setText("EclipseBug551303");
		shell.setLayout(new GridLayout(3, true));

		// First button
		Button sourceButton = new Button(shell, SWT.PUSH);
		sourceButton.setText("Source");

		// Second button
		Button targetButton = new Button(shell, SWT.PUSH);
		targetButton.setText("SWT Target");

		// Third button (swing)
		Composite swingContainer = new Composite(shell, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(swingContainer);

		SwingUtilities.invokeLater(() -> {

			JApplet applet = new JApplet();
			frame.add(applet);
			JButton swingButton = new JButton("Swing target");
			applet.add(swingButton);

			new java.awt.dnd.DropTarget(swingButton, new DropTargetListener() {

				@Override
				public void dropActionChanged(DropTargetDragEvent dtde) {
					System.out.println("Swing: dropActionChanged()");
				}

				@Override
				public void drop(DropTargetDropEvent dtde) {
					dtde.acceptDrop(DnDConstants.ACTION_MOVE);
					System.out.println("Swing: drop " + dtde.getTransferable());
				}

				@Override
				public void dragOver(DropTargetDragEvent dtde) {
					System.out.println("Swing: dropOver()");

				}

				@Override
				public void dragExit(java.awt.dnd.DropTargetEvent dte) {
					System.out.println("Swing: dropExit()");
				}

				@Override
				public void dragEnter(DropTargetDragEvent dtde) {
					System.out.println("Swing: dragEnter()");
				}
			});
		});

		shell.open();
		shell.pack();

		DragSource ds = new DragSource(sourceButton, DND.DROP_MOVE);
		ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		ds.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = "Hello";
			}

			@Override
			public void dragStart(DragSourceEvent event) {
				System.out.println("SWT: dtagStart");

			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				// IF drop succeeded, but this method wasn't called then this's an indication 
				// that JVM will crash in 5 mins because of a bug in GTK.
				System.err.println("SWT: dragFinished");
			}
		});

		DropTarget dt = new DropTarget(targetButton, DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				System.err.println("SWT: Drop success: " + event.data);
			}
		});

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
