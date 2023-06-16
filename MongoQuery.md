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

