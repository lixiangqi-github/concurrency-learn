package com.homurax.chapter10.event.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {

	private String msg;
	private String source;
	private Date date;
}
