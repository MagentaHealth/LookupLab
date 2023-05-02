# Copyright (C) 2022-2023 Magenta Health Inc. 
# Authored by Carmen La <https://carmen.la/>.

# This file is part of LookupLab.

# LookupLab is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.

# LookupLab is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.

# You should have received a copy of the GNU Affero General Public License
# along with LookupLab.  If not, see <https://www.gnu.org/licenses/>.

#!/bin/bash

echo "Generating DB password"
DB_PASSWORD=$(openssl rand -hex 10)

echo "POSTGRES_DB=dfd" >> db.env
echo "POSTGRES_USER=dfd" >> db.env
echo "POSTGRES_PASSWORD=$DB_PASSWORD" >> db.env

echo "{:jdbc-url \"jdbc:postgresql://db/dfd?user=dfd&password=$DB_PASSWORD\"}" >> .db-connection.edn