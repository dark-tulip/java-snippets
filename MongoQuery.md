```
db.Person
  .aggregate([
    {
      $match: {
        $and: [{status: 'ACTIVE'}, {actual: true}],
        $nor: [{isRoot: true}],
        $nor: [{isSystem: true}]
      }
    },
    {$addFields: {type: "PERSON"}},
    {
      $project:
        {_id: 1, name: {$concat: ["$name", "$surname"]}, type: 1, email: 1, companyId: 1}
    },

    {
      $unionWith: {
        coll: "Department",
        pipeline: [
          {$match: {"actual": true}},
          {$addFields: {type: "DEPARTMENT"}},
          {$project: {_id: 1, name: 1, type: 1, companyId: 1}}
        ]
      },
    },

    {
      $unionWith: {
        coll: "PersonGroup", pipeline: [
          {$match: {"active": true}},
          {$addFields: {type: "GROUP"}},
          {$project: {_id: 1, name: 1, type: 1, companyId: 1}}
        ]
      }
    },
    {
      $match: {companyId: ObjectId('6f27ebce5e49a79d69e522a8')}
    },
    {
      $addFields: {
        sortByIds: {
          $cond: {
            if: {
              $in: ["$_id", [
                ObjectId('fb1a992e15b53adf9df0dc61'),
                ObjectId('abcfaaca0ab24f66475de98b'),
                ObjectId('a74227440d8a3a94f3227d7e'),
                ObjectId('f06da2d108585002105f341c'),
              ]]
            },
            then: 0,
            else: 9999999
          }
        }
      }
    },
    {$sort: {sortByIds: 1, type: 1, name: 1}}
  ]);
```


### Получить внутренние записи коллекции
```
db.getSiblingDB("db_name").getCollection("coll_name").aggregate([
  {
    $project: {
      events: {
        $objectToArray: "$events"  // превратить в key/value документ, чтобы ссылаться ниже через точку, где K это ИД события, а через V можно достать его внутренности
      }
    }
  },
  {
    $unwind: "$events"  // дернуть документ за веточку выше
  },
  {
    $project: {
      _id: 1,
      eventType: "$events.v.type",
      fieldValues: { $objectToArray: "$events.v.fieldValues" },
      participantIds: { $objectToArray: "$events.v.participantIds" },
      happened: "$events.v.happened"
    }
  },
  {
    $unwind: {
      path: "$fieldValues",
      preserveNullAndEmptyArrays: true
    }
  },
  {
    $unwind: {
      path: "$participantIds",
      preserveNullAndEmptyArrays: true  // разрешить пустые значения в ячейках, нужно для полной проекции
    }
  },
  {
    $project: {
      _id: 1,
      eventType: 1,
      fieldId: "$fieldValues.k",
      newValue: "$fieldValues.v.newStoredValue",
      oldValue: "$fieldValues.v.oldStoredValue",
      participantId: "$participantIds.k",
      happenedAt: "$happened.at",
      happenedBy: "$happened.by"
    }
  },
])
```

