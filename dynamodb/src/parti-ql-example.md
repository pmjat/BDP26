## Dynamo-DB `Parti-SQL` examples

Example Item (Movie)

```json
{
    "year": 2013,
    "title": "Prisoners",
    "info": {
        "directors": ["Denis Villeneuve"],
        "release_date": "2013-08-30T00:00:00Z",
        "rating": 8.2,
        "genres": ["Crime", "Drama", "Mystery"]
    }
}
```



### PUT (new item) Native operation 

```sql
INSERT INTO Movies VALUE {
    'year': 2013,  -- Partition Key
    'title': 'Prisoners',  -- Sort Key
    'info': {
        'directors': ['Denis Villeneuve'],
        'release_date': '2013-08-30T00:00:00Z',
        'rating': 8.2,
        'genres': ['Crime', 'Drama', 'Mystery']
    }
}
```

*Note: PartiQL `INSERT` fails if an item with the same primary key already exists. It does not replace that item.*

### SELECTION based on Primary Key (Native operation GET)

```sql
SELECT *
FROM Movies
WHERE year = 2013
  AND title = 'Prisoners'
```

*Note: both Hash-Key and Sort-Key needs to be specified for GET*

### Projection

```sql
SELECT title, info.rating
FROM Movies
WHERE year = 2013
  AND title = 'Prisoners'
```



### SELECTION based native operation: `query` with FILTER

```sql
SELECT *
FROM Movies
WHERE year = 2013
  AND info.rating > 8.0
```

### UPDATE a movie's rating

Suppose the existing rating is `8.2`, and you want to change it to `8.5`.

```sql
UPDATE Movies
SET info.rating = 8.5
WHERE year = 2013
  AND title = 'Prisoners'
```

### DELETE 

### PartiQL statement

```
DELETE FROM Movies
WHERE year = 2013
  AND title = 'Prisoners'
```

