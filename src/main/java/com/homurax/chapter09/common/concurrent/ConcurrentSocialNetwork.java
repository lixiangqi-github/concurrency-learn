package com.homurax.chapter09.common.concurrent;

import com.homurax.chapter09.common.data.Person;
import com.homurax.chapter09.common.data.PersonPair;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ConcurrentSocialNetwork {

	public static List<PersonPair> bidirectionalCommonContacts(
			List<Person> people) {

		Map<String, List<PersonPair>> group = people
				.parallelStream()
				.map(new CommonPersonMapper())
				.flatMap(Collection::stream)
				.collect(Collectors.groupingByConcurrent(PersonPair::getFullId));
		
		Collector<Collection<String>, AtomicReference<Collection<String>>, Collection<String>> intersecting = Collector.of(
			    () -> new AtomicReference<>(null), 
			    (acc, list) -> {
			    	if (acc.get() == null) {
			    		acc.updateAndGet(value -> new ConcurrentLinkedQueue<>(list));
			    	} else {
			    		acc.get().retainAll(list);
			    	}
			    },
			    (acc1, acc2) -> {
			      if (acc1.get() == null)
			        return acc2;
			      if (acc2.get() == null)
			        return acc1;
			      acc1.get().retainAll(acc2.get());
			      return acc1;
			    }, 
			    (acc) -> acc.get() == null ? Collections.emptySet() : acc.get(), 
			    Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED);

		List<PersonPair> peopleCommonContacts = group.entrySet()
			      .parallelStream()
			      .map((entry) -> {
			        Collection<String> commonContacts = entry.getValue()
			            .parallelStream()
			            .map(Person::getContacts)
			            .collect(intersecting);

			        PersonPair person = new PersonPair();
			        person.setId(entry.getKey().split(",")[0]);
			        person.setOtherId(entry.getKey().split(",")[1]);
			        person.setContacts(new ArrayList<>(commonContacts));
			        return person;
			      }).collect(Collectors.toList());

		return peopleCommonContacts;

	}

}
