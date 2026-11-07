package com.ecoembes.facade;

import com.ecoembes.service.DumpsterService;

public class DumpsterController {
	private DumpsterService dumpsterService;
	
	public DumpsterController (DumpsterService ds) {
		this.dumpsterService = ds;
	}
}
