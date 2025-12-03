package dtos;

import java.time.LocalDate;

public class ClickEventDto {
	private LocalDate clickDate;
	private int count;
	public ClickEventDto(LocalDate clickDate, int count) {
		super();
		this.clickDate = clickDate;
		this.count = count;
	}
	
	public ClickEventDto() {
		// TODO Auto-generated constructor stub
	}

	public LocalDate getClickDate() {
		return clickDate;
	}

	public void setClickDate(LocalDate clickDate) {
		this.clickDate = clickDate;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
}
