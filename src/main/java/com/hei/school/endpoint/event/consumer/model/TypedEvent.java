package com.hei.school.endpoint.event.consumer.model;

import com.hei.school.PojaGenerated;
import com.hei.school.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
