/*
 *  Copyright (C) 2023 Hong Qiaowei <hongqiaowei@163.com>. All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.example.util;

import org.apache.commons.lang3.RandomUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hong Qiaowei
 */
public abstract class ThreadContext {

	private static ThreadLocal<Map<Object, Object>> tl = new ThreadLocal<>();

	private static final int     map_cap         = 32;

	private static final String  gti             = "gtiT";

	private static final String  sb              = "sbT";
	public  static final String  SB_0            = "sb0T";
	private static final int     sb_cap          = 256;

	private static final String  array_list      = "arlstT";
	public  static final String  ARRAY_LIST_0    = "arlst0T";
	private static final String  hash_map        = "hsMapT";
	private static final String  linked_hash_map = "lhsMapT";
	private static final String  hash_set        = "hsSetT";

	private static final String  trace_id        = "traIdT";

	private ThreadContext() {
	}

	public static String globalThreadId() {
		Map<Object, Object> m = getMap();
		String id = (String) m.get(gti);
		if (id == null) {
			id = NetworkUtils.getServerId() + Const.S.DOT_STR + Thread.currentThread().getId() + Const.S.DOT_STR + RandomUtils.nextInt(1, 1000);
			m.put(gti, id);
		}
		return id;
	}

	public static void setTraceId(String traceId) {
		set(trace_id, traceId);
	}

	public String getTraceId() {
		return (String) get(trace_id);
	}

	/** use me carefully! */
	public static StringBuilder getStringBuilder() {
		return getStringBuilder(true);
	}

	/** use me carefully! */
	public static StringBuilder getStringBuilder(boolean clean) {
		Map<Object, Object> m = getMap();
		StringBuilder b = (StringBuilder) m.get(sb);
		if (b == null) {
			b = new StringBuilder(sb_cap);
			m.put(sb, b);
		} else {
			if (clean) {
				b.delete(0, b.length());
			}
		}
		return b;
	}
	
	public static StringBuilder getStringBuilder(Object key) {
		StringBuilder b = (StringBuilder) get(key);
		if (b == null) {
			b = new StringBuilder(sb_cap);
			Map<Object, Object> m = getMap();
			m.put(key, b);
		} else {
			b.delete(0, b.length());
		}
		return b;
	}
	
	/** for legacy code. */
	public static SimpleDateFormat getSimpleDateFormat(String pattern) {
		Map<Object, Object> m = getMap();
		SimpleDateFormat sdf = (SimpleDateFormat) m.get(pattern);
		if (sdf == null) {
			sdf = new SimpleDateFormat(pattern);
			m.put(pattern, sdf);
		}
		return sdf;
	}

	private static Map<Object, Object> getMap() {
		Map<Object, Object> m = tl.get();
		if (m == null) {
			m = new HashMap<>(map_cap);
			tl.set(m);
		}
		return m;
	}
	
	public static Object get(Object key) {
		return getMap().get(key);
	}
	
	public static <T> T get(Object key, Class<T> clz) {
		T t = (T) get(key);
		if (t == null) {
			try {
				t = clz.newInstance();
				set(key, t);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return t;
	}

	public static void set(Object key, Object obj) {
		getMap().put(key, obj);
	}
	
	public static Object remove(Object key) {
		return getMap().remove(key);
	}

	public static <T> ArrayList<T> getArrayList() {
		return getArrayList(array_list, true);
	}

	public static <T> ArrayList<T> getArrayList(Object key) {
		return getArrayList(key, true);
	}

	public static <T> ArrayList<T> getArrayList(Object key, boolean clear) {
		ArrayList<T> l = (ArrayList<T>) get(key);
		if (l == null) {
			l = new ArrayList<T>();
			set(key, l);
		} else if (clear) {
			l.clear();
		}
		return l;
	}

	public static <K, V> HashMap<K, V> getHashMap() {
		return getHashMap(hash_map, true);
	}

	public static <K, V> HashMap<K, V> getHashMap(Object key) {
		return getHashMap(key, true);
	}

	public static <K, V> HashMap<K, V> getHashMap(Object key, boolean clear) {
		HashMap<K, V> m = (HashMap<K, V>) get(key);
		if (m == null) {
			m = new HashMap<K, V>();
			set(key ,m);
		} else if (clear) {
			m.clear();
		}
		return m;
	}

	public static <K, V> LinkedHashMap<K, V> getLinkedHashMap() {
		return getLinkedHashMap(linked_hash_map, true);
	}

	public static <K, V> LinkedHashMap<K, V> getLinkedHashMap(Object key) {
		return getLinkedHashMap(key, true);
	}

	public static <K, V> LinkedHashMap<K, V> getLinkedHashMap(Object key, boolean clear) {
		LinkedHashMap<K, V> m = (LinkedHashMap<K, V>) get(key);
		if (m == null) {
			m = new LinkedHashMap<K, V>();
			set(key ,m);
		} else if (clear) {
			m.clear();
		}
		return m;
	}

	public static <E> HashSet<E> getHashSet() {
		return getHashSet(hash_set, true);
	}

	public static <E> HashSet<E> getHashSet(Object key) {
		return getHashSet(key, true);
	}

	public static <E> HashSet<E> getHashSet(Object key, boolean clear) {
		HashSet<E> s = (HashSet<E>) get(key);
		if (s == null) {
			s = new HashSet<E>();
			set(key ,s);
		} else if (clear) {
			s.clear();
		}
		return s;
	}
}
