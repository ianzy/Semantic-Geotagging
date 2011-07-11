package geotagging.app;

import geotagging.DES.ResponseCategory;

import java.util.List;

public interface IResponseCounter {
	void updateCounter(List<ResponseCategory> counters);
}
