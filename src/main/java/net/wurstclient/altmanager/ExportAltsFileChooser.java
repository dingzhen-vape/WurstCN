/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.altmanager;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.wurstclient.util.SwingUtils;

public final class ExportAltsFileChooser extends JFileChooser
{
	public static void main(String[] args)
	{
		SwingUtils.setLookAndFeel();
		
		int response = JOptionPane.showConfirmDialog(null,
			"这将创建一个未加密（纯文本）的副本你的alt列表。\n"
				+ "以纯文本存储密码是有风险的，因为它们很容易被病毒窃取。\n"
				+ "把这个副本存放在安全的地方，并把它放在你的Minecraft文件夹之外！",
			"Warning", JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE);
		
		if(response != JOptionPane.OK_OPTION)
			return;
		
		JFileChooser fileChooser = new ExportAltsFileChooser();
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		
		FileNameExtensionFilter txtFilter =
			new FileNameExtensionFilter("TXT file (username:password)", "txt");
		fileChooser.addChoosableFileFilter(txtFilter);
		
		FileNameExtensionFilter jsonFilter =
			new FileNameExtensionFilter("JSON file", "json");
		fileChooser.addChoosableFileFilter(jsonFilter);
		
		if(fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			return;
		
		String path = fileChooser.getSelectedFile().getAbsolutePath();
		FileFilter fileFilter = fileChooser.getFileFilter();
		
		if(fileFilter == txtFilter && !path.endsWith(".txt"))
			path += ".txt";
		else if(fileFilter == jsonFilter && !path.endsWith(".json"))
			path += ".json";
		
		System.out.println(path);
	}
	
	@Override
	protected JDialog createDialog(Component parent) throws HeadlessException
	{
		JDialog dialog = super.createDialog(parent);
		dialog.setAlwaysOnTop(true);
		return dialog;
	}
	
}
