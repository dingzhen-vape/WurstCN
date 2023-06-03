/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks.autolibrarian;

import net.wurstclient.settings.BookOffersSetting;
import net.wurstclient.settings.EnumSetting;

public final class UpdateBooksSetting
	extends EnumSetting<UpdateBooksSetting.UpdateBooks>
{
	public UpdateBooksSetting()
	{
		super("Update books",
			"当一个村民学会了出售你想要的书籍时，自动更新书籍列表。\n\n"
				+ "\u00a7lOff\u00a7r - 不要更新列表。\n\n"
				+ "\u00a7lRemove\u00a7r - 从列表中移除这本书，这样下一个村民会学习另一本书。\n\n"
				+ "\u00a7lPrice\u00a7r - 更新这本书的最高价格，这样下一个村民必须以更便宜的价格出售它。",
			UpdateBooks.values(), UpdateBooks.REMOVE);
	}
	
	public enum UpdateBooks
	{
		OFF("关闭"),
		REMOVE("移除"),
		PRICE("价格");
		
		private String name;
		
		private UpdateBooks(String name)
		{
			this.name = name;
		}
		
		public void update(BookOffersSetting wantedBooks, BookOffer offer)
		{
			int index = wantedBooks.indexOf(offer);
			
			switch(this)
			{
				case OFF:
				return;
				
				case REMOVE:
				wantedBooks.remove(index);
				break;
				
				case PRICE:
				if(offer.price() <= 1)
					wantedBooks.remove(index);
				else
					wantedBooks.replace(index, new BookOffer(offer.id(),
						offer.level(), offer.price() - 1));
				break;
			}
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
}
